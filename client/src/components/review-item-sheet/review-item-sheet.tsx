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
  SheetDescription,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { parsingSubtext } from "@/lib/text";
import { useQueryClient } from "@tanstack/react-query";
import { isSameDay } from "date-fns";
import { Info, LoaderCircle } from "lucide-react";

interface ItemSheetProps {
  trigger: ReactNode;
  itemId: number;
}

export default function ReviewItemSheet({ itemId, trigger }: ItemSheetProps) {
  const queryClient = useQueryClient();
  const [open, setOpen] = useState(false);
  const {
    isLoading: isItemDetailLoading,
    data: itemDetail,
    error: itemDetailError,
  } = useReviewItemDetail({ id: itemId, enabled: open });
  const { markAsMemorized, deleteItem, notMemorized } = useReviewItemMutation();

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
  const notMemorizedDates = itemDetail?.notMemorizedDates.map(
    (date) => new Date(date),
  );

  const isReviewToday = upcomingReviewDates?.some((date) =>
    isSameDay(date, new Date()),
  );
  const isMemorizedToday = memorizedDates?.some((date) =>
    isSameDay(date, new Date()),
  );
  const isNotMemorizedToday = notMemorizedDates?.some((date) =>
    isSameDay(date, new Date()),
  );
  const hasActionToday =
    isReviewToday && (isMemorizedToday || isNotMemorizedToday);

  return (
    <Sheet
      open={open}
      onOpenChange={(open) => {
        if (!open) {
          queryClient.refetchQueries({ queryKey: ["review-items"] });
        }
        setOpen(open);
      }}
    >
      <SheetTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
        {trigger}
      </SheetTrigger>

      <SheetContent
        forceMount
        side={side}
        className="md:w-full max-h-[100vh] overflow-auto"
        aria-describedby="review-item-sheet"
      >
        <SheetDescription hidden>Review Item({itemId}) Sheet</SheetDescription>
        {isItemDetailLoading && (
          <SheetTitle className="flex gap-x-2">
            <LoaderCircle className="animate-spin" /> Loading ...
          </SheetTitle>
        )}

        {itemDetailError && (
          <>
            <SheetTitle>Error</SheetTitle>
            <p>{itemDetailError.message}</p>
          </>
        )}

        {itemDetail && (
          <section className="flex flex-col justify-between h-full">
            <SheetHeader>
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
                <div className="flex gap-x-2">
                  <p>Next review at </p>
                  <Badge>
                    {new Date(
                      itemDetail?.upcomingReviewDates[0],
                    ).toLocaleDateString("en-CA", {
                      dateStyle: "short",
                    })}
                  </Badge>
                </div>
              </div>
            </SheetHeader>

            <section>
              <div className="flex flex-col items-center my-4">
                <h4 className="text-xl font-semibold tracking-tight text-black scroll-m-20">
                  Review Dates
                </h4>

                <div className="text-center">
                  {upcomingReviewDates &&
                    memorizedDates &&
                    skippedDates &&
                    notMemorizedDates && (
                      <Calendar
                        modifiers={{
                          review: upcomingReviewDates,
                          memorized: memorizedDates,
                          skipped: skippedDates,
                          notMemorized: notMemorizedDates,
                        }}
                        modifiersClassNames={{
                          review: "after-content after:border-blue-400",
                          memorized: "bg-green-200",
                          skipped: "bg-red-200",
                          notMemorized: "bg-yellow-300",
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
                    <div className="h-[20px] w-[20px] bg-yellow-300"></div>
                    <p>Not memorized Dates</p>
                  </div>
                </div>
              </div>
            </section>

            <footer className="pb-6 mt-6">
              <div className="flex flex-col w-full gap-y-4">
                {isReviewToday && (
                  <Button
                    onClick={() => markAsMemorized(itemId)}
                    disabled={hasActionToday}
                  >
                    Memorized
                  </Button>
                )}

                {isReviewToday && (
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Button
                        onClick={() => notMemorized(itemId)}
                        disabled={hasActionToday}
                        variant="secondary"
                      >
                        Not memorized
                        <Info />
                      </Button>
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>This action will renew the review cycle.</p>
                    </TooltipContent>
                  </Tooltip>
                )}

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
