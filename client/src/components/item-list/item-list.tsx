import { useQuery } from "@tanstack/react-query";
import { axiosApiClient } from "../../api-client";

export const ItemList = () => {
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["todos"],
    queryFn: axiosApiClient.getItems,
  });

  return (
    <div>
      <h1>Ebbinghaus Project</h1>
      {areItemsLoading && <p>Loading...</p>}
      {items?.map((item) => (
        <div key={item.id}>
          <h2>{item.mainText}</h2>
          <p>{item.subText}</p>
          <p>Created at: {item.createDatetime}</p>
          <p>Successes: {item.successCount}</p>
          <p>Failures: {item.failCount}</p>
          <p>Next remind datetimes: {item.nextRemindDatetimes.join(", ")}</p>
          <p>Repeated: {item.repeated ? "Yes" : "No"}</p>
        </div>
      ))}
    </div>
  );
};
