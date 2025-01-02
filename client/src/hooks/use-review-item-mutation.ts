import { apiClient } from "@/api-client";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

export const useReviewItemMutation = () => {
  const queryClient = useQueryClient();

  const { mutate: markAsMemorized } = useMutation({
    mutationFn: async (id: number) => await apiClient.markAsMemorized(id),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["review-items", variables],
      });
      toast.success("Item marked as memorized.");
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to mark item as memorized.");
    },
  });

  const { mutate: notMemorized } = useMutation({
    mutationFn: async (id: number) => await apiClient.notMemorized(id),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: ["review-items", variables],
      });
      toast.success("Mark item as not memorized.");
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to mark item as not memorized.");
    },
  });

  const { mutate: deleteItem } = useMutation({
    mutationFn: async (id: number) => await apiClient.deleteItem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["review-items"],
      });
      toast.success("Item deleted.");
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to delete item.");
    },
  });

  return {
    markAsMemorized,
    notMemorized,
    deleteItem,
  };
};
