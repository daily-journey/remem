import { apiClient } from "@/api-client";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

export const useReviewItemMutation = () => {
  const queryClient = useQueryClient();

  const { mutateAsync: markAsMemorized } = useMutation({
    mutationFn: async (id: number) => await apiClient.markAsMemorized(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ["review-items", id] });
      toast.success("Item marked as memorized.");
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to mark item as memorized.");
    },
  });

  const { mutateAsync: notMemorized } = useMutation({
    mutationFn: async (id: number) => await apiClient.notMemorized(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: ["review-items", id] });
      toast.success("Mark item as not memorized.");
    },
    onError: (error) => {
      console.error(error);
      toast.error("Failed to mark item as not memorized.");
    },
  });

  const { mutateAsync: deleteItem } = useMutation({
    mutationFn: async (id: number) => await apiClient.deleteItem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["review-items", "today"] });
      queryClient.invalidateQueries({ queryKey: ["review-items", "all"] });
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
