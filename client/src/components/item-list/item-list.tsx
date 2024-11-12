import { apiClient } from "@/api-client";
import ItemDetail from "@/components/item-detail/item-detail";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { useQuery } from "@tanstack/react-query";

export const ItemList = () => {
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["todos"],
    queryFn: apiClient.getItems,
  });

  const sorted = items?.toSorted((a, b) => {
    const aNextReviewDate = new Date(a.reviewDates[0]);
    const bNextReviewDate = new Date(b.reviewDates[0]);

    return aNextReviewDate.getTime() - bNextReviewDate.getTime();
  });

  const randomColor = (): { backgroundColor: string; color: string } => {
    const randomColor = Math.floor(Math.random() * 16777215).toString(16);
    const textColor =
      parseInt(randomColor, 16) > 0xffffff / 2 ? "black" : "white";

    return {
      backgroundColor: `#${randomColor}`,
      color: textColor,
    };
  };

  return (
    <section>
      {areItemsLoading && <p>Loading...</p>}

      <ul className="flex flex-wrap justify-between gap-2">
        {sorted?.map((item) => {
          const nextReviewDate = new Date(item.reviewDates[0])
            .toISOString()
            .split("T")[0];

          return (
            <Drawer key={item.id}>
              <DrawerTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
                <li>
                  <Card>
                    <CardHeader>
                      <CardTitle>{item.mainText}</CardTitle>
                    </CardHeader>

                    <CardContent>
                      {item.subText} ... and next review at{" "}
                      <Badge style={randomColor()}>{nextReviewDate}</Badge>
                    </CardContent>
                  </Card>
                </li>
              </DrawerTrigger>

              <DrawerContent>
                <div className="w-full max-w-sm mx-auto">
                  <DrawerHeader>
                    <DrawerTitle asChild>
                      <h2 className="pb-2 text-3xl font-semibold tracking-tight border-b scroll-m-20 first:mt-0">
                        {item.mainText}{" "}
                        <span className="text-gray-400">(#{item.id})</span>
                      </h2>
                    </DrawerTitle>

                    <DrawerDescription>
                      <div className="mb-2">
                        <p className="leading-7 [&:not(:first-child)]:mt-6">
                          {item.subText}
                        </p>
                        <p className="leading-7 [&:not(:first-child)]:mt-6">
                          Next review at{" "}
                          <Badge style={randomColor()}>{nextReviewDate}</Badge>
                        </p>
                      </div>

                      <ItemDetail data={item} />
                    </DrawerDescription>
                  </DrawerHeader>

                  <DrawerFooter>
                    <Button>Edit</Button>
                    <Button variant="destructive">Delete</Button>
                  </DrawerFooter>
                </div>
              </DrawerContent>
            </Drawer>
          );
        })}
      </ul>
    </section>
  );
};
