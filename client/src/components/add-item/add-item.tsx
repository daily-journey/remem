import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { apiClient } from "@/api-client";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

const formSchema = z.object({
  mainText: z.string().min(1, {
    message: "Main text must be at least 1 characters.",
  }),
  subText: z.string().optional(),
});

export default function AddItem() {
  const queryClient = useQueryClient();

  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      mainText: "",
      subText: "",
    },
  });

  async function onSubmit(values: z.infer<typeof formSchema>) {
    try {
      setIsLoading(true);

      await apiClient.addItem({
        mainText: values.mainText,
        subText: values.subText,
      });
      await queryClient.refetchQueries({ queryKey: ["review-items"] });
      toast.success("Item added.");
      setError(null);
      setOpen(false);
    } catch (error) {
      console.error(error);
      setError("An error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    if (open) {
      form.reset();
    }
  }, [form, open]);

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline">Add Item</Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Add Item</DialogTitle>
          <DialogDescription>
            Fill out the form below to add a new item to the list.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
            <FormField
              control={form.control}
              name="mainText"
              render={({ field }) => {
                return (
                  <FormItem>
                    <FormLabel>Main Text (Required)</FormLabel>
                    <FormControl>
                      <Input placeholder="Enter main text." {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                );
              }}
            />
            <FormField
              control={form.control}
              name="subText"
              render={({ field }) => {
                return (
                  <FormItem>
                    <FormLabel>Sub Text</FormLabel>
                    <FormControl>
                      <Textarea placeholder="Enter sub text." {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                );
              }}
            />

            <DialogFooter>
              <Button type="submit" disabled={isLoading}>
                Submit
              </Button>
            </DialogFooter>
            {error && <FormMessage className="text-right">{error}</FormMessage>}
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
