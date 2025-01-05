import { apiClient } from "@/api-client";
import { useQuery } from "@tanstack/react-query";
export default function useReviewItemDetail({
  id,
  enabled,
}: {
  id: number;
  enabled: boolean;
}) {
  const { data, error, isLoading } = useQuery({
    queryKey: ["review-items", id],
    queryFn: () => apiClient.getReviewItemDetail(id),
    enabled,
  });

  return {
    data,
    error,
    isLoading,
  };
}
