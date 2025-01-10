import { useQuery } from "@tanstack/react-query";
import { useCookies } from "react-cookie";

import { apiClient } from "@/api-client";
import ReviewItemContextMenu from "@/components/review-item-context-menu/review-item-context-menu";
import ReviewItemSheet from "@/components/review-item-sheet/review-item-sheet";

import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import { CircleArrowRight, CircleCheckBig, Inbox } from "lucide-react";

export default function TodayReviewItems() {
  const [cookies] = useCookies(["Authorization"]);
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["review-items", "today"],
    queryFn: () => apiClient.getTodayReviewItems(),
    enabled: !!cookies.Authorization,
  });

  if (areItemsLoading) {
    return (
      <section>
        <p>Loading ...</p>
      </section>
    );
  }

  if (!items) {
    return (
      <section>
        <p>No items to review.</p>
      </section>
    );
  }

  const itemsByAction = Object.groupBy(items, (item) => item.status);

  return (
    <section>
      <h2 className="flex items-center mt-6 mb-2 font-bold gap-x-2">
        <Inbox />
        No Action
      </h2>
      {itemsByAction.NO_ACTION ? (
        <ul className="flex flex-wrap justify-between gap-2 ">
          {itemsByAction.NO_ACTION?.map((item) => {
            return (
              <ReviewItemSheet
                key={item.id}
                itemId={item.id}
                trigger={
                  <ReviewItemContextMenu
                    reviewItemId={item.id}
                    reviewItemStatus={item.status}
                  >
                    <li>
                      <TodayReviewItem mainText={item.mainText} />
                    </li>
                  </ReviewItemContextMenu>
                }
              />
            );
          })}
        </ul>
      ) : (
        <p>No items to review</p>
      )}

      <h2 className="flex items-center mt-6 mb-2 font-bold gap-x-2">
        <CircleCheckBig />
        Memorized
      </h2>
      {itemsByAction.MEMORIZED ? (
        <ul className="flex flex-wrap justify-between gap-2 ">
          {itemsByAction.MEMORIZED.map((item) => {
            return (
              <ReviewItemSheet
                key={item.id}
                itemId={item.id}
                trigger={
                  <ReviewItemContextMenu
                    reviewItemId={item.id}
                    reviewItemStatus={item.status}
                  >
                    <li>
                      <TodayReviewItem mainText={item.mainText} />
                    </li>
                  </ReviewItemContextMenu>
                }
              />
            );
          })}
        </ul>
      ) : (
        <p>No items you memorized today.</p>
      )}

      <h2 className="flex items-center mt-6 mb-2 font-bold gap-x-2">
        <CircleArrowRight />
        Not Memorized
      </h2>
      {itemsByAction.NOT_MEMORIZED ? (
        <ul className="flex flex-wrap justify-between gap-2">
          {itemsByAction.NOT_MEMORIZED?.map((item) => {
            return (
              <ReviewItemSheet
                key={item.id}
                itemId={item.id}
                trigger={
                  <ReviewItemContextMenu
                    reviewItemId={item.id}
                    reviewItemStatus={item.status}
                  >
                    <li>
                      <TodayReviewItem mainText={item.mainText} />
                    </li>
                  </ReviewItemContextMenu>
                }
              />
            );
          })}
        </ul>
      ) : (
        <p>No items you didn&apos;t memorize today.</p>
      )}
    </section>
  );
}

interface TodayReviewItemProps {
  mainText: string;
}
function TodayReviewItem({ mainText }: TodayReviewItemProps) {
  return (
    <Card className="transition-all hover:bg-muted dark:hover:bg-muted">
      <CardHeader>
        <CardTitle className="break-all">{mainText}</CardTitle>
      </CardHeader>
    </Card>
  );
}
