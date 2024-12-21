import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useCookies } from "react-cookie";

import { apiClient } from "@/api-client";
import useInnerWidth from "@/hooks/use-inner-width";
import { useReviewItemMutation } from "@/hooks/use-review-item-mutation";
import { parsingSubtext } from "@/lib/text";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Sheet,
  SheetContent,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";

export default function ReviewItems() {
  const [cookies] = useCookies(["Authorization"]);
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["review-items"],
    queryFn: () => apiClient.getAllItems(),
    enabled: !!cookies.Authorization,
  });

  const { remindTomorrow, markAsMemorized, deleteItem } =
    useReviewItemMutation();

  const sortedItems = items?.toSorted((a, b) => {
    const aNextReviewDate = new Date(a.reviewDates[0]);
    const bNextReviewDate = new Date(b.reviewDates[0]);

    return aNextReviewDate.getTime() - bNextReviewDate.getTime();
  });

  const { innerWidth } = useInnerWidth();
  const [side, setSide] = useState<"bottom" | "right">("right");

  useEffect(() => {
    if (innerWidth < 768) {
      setSide("bottom");
    } else {
      setSide("right");
    }
  }, [innerWidth]);

  return (
    <section>
      <ul className="flex flex-wrap justify-between gap-2 ">
        {areItemsLoading && <p>Loading...</p>}

        {sortedItems?.length === 0 && <p>No items to review.</p>}

        {sortedItems?.map((item) => {
          const reviewDates = item.reviewDates.map((date) => new Date(date));
          const nextReviewDate = new Date(item.reviewDates[0]).toLocaleString();

          return (
            <Sheet key={item.id}>
              <SheetTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
                <li>
                  <Card className="transition-all hover:bg-gray-300">
                    <CardHeader>
                      <CardTitle>{item.mainText}</CardTitle>
                    </CardHeader>
                  </Card>
                </li>
              </SheetTrigger>
              <SheetContent
                side={side}
                className="md:w-full max-h-[100vh] overflow-auto"
              >
                <section className="flex flex-col justify-between">
                  <header>
                    <SheetTitle asChild>
                      <h2 className="pb-2 text-3xl font-semibold tracking-tight border-b scroll-m-20 first:mt-0">
                        {item.mainText}{" "}
                        <span className="text-gray-400">(#{item.id})</span>
                      </h2>
                    </SheetTitle>

                    <div className="my-4">
                      <p className="leading-7 [&:not(:first-child)]:mt-6">
                        {parsingSubtext(item.subText)}
                      </p>
                      <p className="leading-7 [&:not(:first-child)]:mt-6">
                        Next review at <Badge>{nextReviewDate}</Badge>
                      </p>
                    </div>
                  </header>

                  <section>
                    <div className="flex flex-col items-center my-4">
                      <h4 className="text-xl font-semibold tracking-tight text-black scroll-m-20">
                        Review Dates
                      </h4>

                      <div className="text-center">
                        <Calendar selected={reviewDates} />
                      </div>
                    </div>
                  </section>

                  <footer>
                    <div className="flex flex-col w-full gap-y-4">
                      <Button onClick={() => markAsMemorized(item.id)}>
                        Memorized
                      </Button>
                      <Button
                        onClick={() => remindTomorrow(item.id)}
                        variant="secondary"
                      >
                        Remind me tomorrow
                      </Button>
                      <Button
                        onClick={() => deleteItem(item.id)}
                        variant="destructive"
                      >
                        Delete
                      </Button>
                    </div>
                  </footer>
                </section>
              </SheetContent>
            </Sheet>
          );
        })}
      </ul>
    </section>
  );
}
