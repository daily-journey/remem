import { useQuery } from "@tanstack/react-query";
import { useCookies } from "react-cookie";

import { apiClient } from "@/api-client";
import { Status } from "@/contract/server";

import ReviewItemContextMenu from "@/components/review-item-context-menu/review-item-context-menu";
import ReviewItemSheet from "@/components/review-item-sheet/review-item-sheet";

import { Card, CardHeader, CardTitle } from "@/components/ui/card";

export default function AllReviewItems() {
  const [cookies] = useCookies(["Authorization"]);
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["review-items", "all"],
    queryFn: () => apiClient.getAllItems(),
    enabled: !!cookies.Authorization,
  });

  const sortedItems = items?.toSorted((a, b) => {
    const aNextReviewDate = new Date(a.reviewDates[0]);
    const bNextReviewDate = new Date(b.reviewDates[0]);

    return aNextReviewDate.getTime() - bNextReviewDate.getTime();
  });

  return (
    <section>
      <ul className="flex flex-col justify-between gap-2 ">
        {areItemsLoading && <p>Loading...</p>}

        {sortedItems?.length === 0 && <p>No items to review.</p>}

        {sortedItems?.map((item) => {
          return (
            <ReviewItemSheet
              key={item.id}
              trigger={
                <ReviewItemContextMenu
                  reviewItemId={item.id}
                  // TODO: should be changed, currently itemDetail has no status.
                  reviewItemStatus={Status.NOT_MEMORIZED}
                >
                  <li>
                    <Card className="transition-all hover:bg-muted dark:hover:bg-muted">
                      <CardHeader>
                        <CardTitle className="break-all">
                          {item.mainText}
                        </CardTitle>
                      </CardHeader>
                    </Card>
                  </li>
                </ReviewItemContextMenu>
              }
              itemId={item.id}
            />
          );
        })}
      </ul>
    </section>
  );
}
