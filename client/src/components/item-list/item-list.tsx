import { apiClient } from "@/api-client";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useQuery } from "@tanstack/react-query";

export const ItemList = () => {
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["todos"],
    queryFn: apiClient.getItems,
  });

  return (
    <section>
      {areItemsLoading && <p>Loading...</p>}

      <ul>
        {items?.map((item) => {
          const dates = item.reviewDates.map((date) => new Date(date));
          return (
            <li key={item.id}>
              <Card className="w-[350px]">
                <CardHeader>
                  <CardTitle>
                    {item.id}. {item.mainText}
                  </CardTitle>
                  <CardDescription>{item.subText}</CardDescription>
                </CardHeader>

                <CardContent>
                  <Calendar selected={dates} />
                </CardContent>

                <CardFooter className="flex justify-between">
                  <Button>Edit</Button>
                  <Button variant="destructive">Delete</Button>
                </CardFooter>
              </Card>
            </li>
          );
        })}
      </ul>
    </section>
  );
};
