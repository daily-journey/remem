import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

const userTokenFormSchema = z.object({
  token: z.string().min(1, {
    message: "Token must be at least 1 characters.",
  }),
});

interface Props {
  onSubmit: (token: string) => void;
}
export default function UserTokenForm({ onSubmit }: Props) {
  const form = useForm<z.infer<typeof userTokenFormSchema>>({
    resolver: zodResolver(userTokenFormSchema),
    defaultValues: {
      token: "",
    },
  });

  function onSubmitUserToken(values: z.infer<typeof userTokenFormSchema>) {
    localStorage.setItem("userToken", values.token);
    onSubmit(values.token);
  }

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmitUserToken)}
        className="w-full space-y-8"
      >
        <FormField
          control={form.control}
          name="token"
          render={({ field }) => {
            return (
              <FormItem>
                <FormLabel>Token</FormLabel>
                <FormControl>
                  <Input placeholder="Your personal token" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            );
          }}
        />

        <Button type="submit">Submit</Button>
      </form>
    </Form>
  );
}
