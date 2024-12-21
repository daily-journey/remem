import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import useInnerWidth from "@/hooks/use-inner-width";
import { useReviewItemMutation } from "@/hooks/use-review-item-mutation";
import { useEffect, useState, type ReactNode } from "react";

interface ItemSheetProps {
  trigger: ReactNode;
  itemId: number;
}

export default function ItemSheet({ itemId, trigger }: ItemSheetProps) {
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

  return (
    <Sheet>
      <SheetTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
        {trigger}
      </SheetTrigger>

      <SheetContent
        side={side}
        className="md:w-full max-h-[100vh] overflow-auto"
      >
        {/* TODO: Implement later */}
        {/* <section className="flex flex-col justify-between">
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
              <Button onClick={() => deleteItem(item.id)} variant="destructive">
                Delete
              </Button>
            </div>
          </footer>
        </section> */}
      </SheetContent>
    </Sheet>
  );
}
