import { useEffect, useState, type ReactNode } from "react";

import useInnerWidth from "@/hooks/use-inner-width";
import useReviewItemDetail from "@/hooks/use-review-item-detail";
import { useReviewItemMutation } from "@/hooks/use-review-item-mutation";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Sheet,
  SheetContent,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { parsingSubtext } from "@/lib/text";
import { LoaderCircle } from "lucide-react";

interface ItemSheetProps {
  trigger: ReactNode;
  itemId: number;
}

export default function ReviewItemSheet({ itemId, trigger }: ItemSheetProps) {
  const {
    isLoading: isItemDetailLoading,
    data: itemDetail,
    error: itemDetailError,
  } = useReviewItemDetail({ id: itemId });
  const { markAsMemorized, deleteItem, remindTomorrow } =
    useReviewItemMutation();

  const { innerWidth } = useInnerWidth();
  const [side, setSide] = useState<"bottom" | "right">("right");

  useEffect(() => {
    if (innerWidth < 768) {
      setSide("bottom");
    } else {
      setSide("right");
    }
  }, [innerWidth]);

  const upcomingReviewDates = itemDetail?.upcomingReviewDates.map(
    (date) => new Date(date),
  );
  const memorizedDates = itemDetail?.memorizedDates.map(
    (date) => new Date(date),
  );
  const skippedDates = itemDetail?.skippedDates.map((date) => new Date(date));
  const remindTomorrowDates = itemDetail?.remindTomorrowDates.map(
    (date) => new Date(date),
  );

  return (
    <Sheet>
      <SheetTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
        {trigger}
      </SheetTrigger>

      <SheetContent
        side={side}
        className="md:w-full max-h-[100vh] overflow-auto"
      >
        {isItemDetailLoading && <LoaderCircle className="animate-spin" />}
        {itemDetailError && <p>{itemDetailError.message}</p>}
        {itemDetail && (
          <section className="flex flex-col justify-between">
            <header>
              <SheetTitle asChild>
                <h2 className="w-full pb-2 pr-2 text-3xl font-semibold tracking-tight break-all border-b scroll-m-20 first:mt-0">
                  {itemDetail.mainText}{" "}
                  <span className="text-gray-400">(#{itemId})</span>
                </h2>
              </SheetTitle>

              <div className="my-4">
                <p className="leading-7 [&:not(:first-child)]:mt-6 break-all">
                  {parsingSubtext(itemDetail.subText)}
                </p>
                <p className="leading-7 [&:not(:first-child)]:mt-6">
                  Next review at{" "}
                  <Badge>{itemDetail?.upcomingReviewDates[0]}</Badge>
                </p>
              </div>
            </header>

            <section>
              <div className="flex flex-col items-center my-4">
                <h4 className="text-xl font-semibold tracking-tight text-black scroll-m-20">
                  Review Dates
                </h4>

                <div className="text-center">
                  {upcomingReviewDates &&
                    memorizedDates &&
                    skippedDates &&
                    remindTomorrowDates && (
                      <Calendar
                        modifiers={{
                          review: upcomingReviewDates,
                          memorized: memorizedDates,
                          skipped: skippedDates,
                          remindTomorrow: remindTomorrowDates,
                        }}
                        modifiersClassNames={{
                          review:
                            "after-content after:border-blue-400 after:m-[1px]",
                          memorized: "bg-green-200",
                          skipped: "bg-red-200",
                          remindTomorrow:
                            "before-content before:border-yellow-500",
                        }}
                      />
                    )}
                </div>

                <div className="flex flex-col mt-2 text-left gap-y-1">
                  <div className="flex text-sm text-gray-400 gap-x-2">
                    <div className="h-[20px] w-[20px] border-2 border-blue-400"></div>
                    <p>Upcoming Review Dates</p>
                  </div>

                  <div className="flex text-sm text-gray-400 gap-x-2">
                    <div className="h-[20px] w-[20px] bg-green-200"></div>
                    <p>Memorized Dates</p>
                  </div>

                  <div className="flex text-sm text-gray-400 gap-x-2">
                    <div className="h-[20px] w-[20px] bg-red-200"></div>
                    <p>Skipped Dates</p>
                  </div>

                  <div className="flex text-sm text-gray-400 gap-x-2">
                    <div className="h-[20px] w-[20px] border-2 border-yellow-400"></div>
                    <p>Remind Tomorrow</p>
                  </div>
                </div>
              </div>
            </section>

            <footer>
              <div className="flex flex-col w-full gap-y-4">
                <Button onClick={() => markAsMemorized(itemId)}>
                  Memorized
                </Button>
                <Button
                  onClick={() => remindTomorrow(itemId)}
                  variant="secondary"
                >
                  Remind me tomorrow
                </Button>
                <Button
                  onClick={() => deleteItem(itemId)}
                  variant="destructive"
                >
                  Delete
                </Button>
              </div>
            </footer>
          </section>
        )}
      </SheetContent>
    </Sheet>
  );
}
