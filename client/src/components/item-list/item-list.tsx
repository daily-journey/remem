import { useQuery } from "@tanstack/react-query";
import { apiClient } from "../../api-client";

export const ItemList = () => {
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["todos"],
    queryFn: apiClient.getItems,
  });

  return (
    <div>
      {areItemsLoading && <p>Loading...</p>}
      {items?.map((item) => (
        <div key={item.id}>
          <h2>{item.mainText}</h2>
          <p>{item.subText}</p>
          <p>Created at: {item.createDatetime}</p>
          <p>Successes: {item.successCount}</p>
          <p>Failures: {item.failCount}</p>
          <p>Next remind datetimes: {item.reviewDates.join(", ")}</p>
          <p>Repeated: {item.repeated ? "Yes" : "No"}</p>
        </div>
      ))}
    </div>
  );
};
