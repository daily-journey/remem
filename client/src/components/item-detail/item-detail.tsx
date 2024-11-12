import { Calendar } from "@/components/ui/calendar";
import type { Item } from "@/model/server";

interface Props {
  data: Item;
}
export default function ItemDetail({ data }: Props) {
  const dates = data.reviewDates.map((date) => new Date(date));

  return (
    <section className="flex flex-col items-center">
      <h4 className="text-xl font-semibold tracking-tight text-black scroll-m-20">
        Review Dates
      </h4>

      <div className="text-center">
        <Calendar selected={dates} />
      </div>
    </section>
  );
}
