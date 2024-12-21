import { apiClient } from "@/api-client";
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
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const userTokenFormSchema = z.object({
  name: z.string().min(1, {
    message: "Name is required",
  }),
  email: z
    .string()
    .min(1, {
      message: "Email is required",
    })
    .email({
      message: "Email is not valid",
    }),
  password: z.string().min(36, {
    message: "Password should be at least 36 characters",
  }),
});

type SignUpFormValues = z.infer<typeof userTokenFormSchema>;

interface Props {
  onSignUpSuccess: () => void;
}

export default function SignUpForm({ onSignUpSuccess }: Props) {
  const { mutate: signUp, status: signUpStatus } = useMutation({
    mutationFn: async (command: SignUpFormValues) =>
      await apiClient.signUp(command),
    onSuccess: () => {
      toast.success("Signed up successfully");
      onSignUpSuccess();
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to sign up");
    },
  });

  const form = useForm<SignUpFormValues>({
    resolver: zodResolver(userTokenFormSchema),
    defaultValues: {
      name: "",
      email: "",
      password: "",
    },
  });

  function onSignUp(values: SignUpFormValues) {
    signUp(values);
  }

  return (
    <>
      <h1 className="mb-4 text-2xl font-bold">Sign Up</h1>
      <Form {...form}>
        <form
          onSubmit={form.handleSubmit(onSignUp)}
          className="w-full space-y-4"
        >
          <FormField
            control={form.control}
            name="name"
            render={({ field }) => {
              return (
                <FormItem>
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Name" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              );
            }}
          />

          <FormField
            control={form.control}
            name="email"
            render={({ field }) => {
              return (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input placeholder="Email" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              );
            }}
          />

          <FormField
            control={form.control}
            name="password"
            render={({ field }) => {
              return (
                <FormItem>
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <Input placeholder="Password" type="password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              );
            }}
          />

          <Button type="submit">Submit</Button>

          {signUpStatus === "pending" && <p>Loading...</p>}
          {signUpStatus === "error" && (
            <p className="mt-4 text-red-600">
              Signup Failed. Please try again.
            </p>
          )}
        </form>
      </Form>
    </>
  );
}
