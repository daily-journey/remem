import { useMutation, useQueryClient } from "@tanstack/react-query";
import { type ReactNode } from "react";

import { apiClient } from "@/api-client";
import type { Item } from "@/model/server";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Card, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Drawer,
  DrawerContent,
  DrawerDescription,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";
import { toast } from "@/hooks/use-toast";

interface Props {
  items?: Item[];
  isLoading: boolean;
}

export default function ReviewItems({ items, isLoading }: Props) {
  const queryClient = useQueryClient();
  const { mutate: markAsMemorized } = useMutation({
    mutationFn: async (id: number) => await apiClient.markAsMemorized(id),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["review-items"],
      });
      toast({
        title: "Item marked as memorized",
      });
    },
    onError: (error) => {
      toast({
        title: "Failed to mark item as memorized",
        description: error.message,
        variant: "destructive",
      });
    },
  });
  const { mutate: remindMeAgainLater } = useMutation({
    mutationFn: async (id: number) => await apiClient.remindLater(id),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["review-items"],
      });
      toast({
        title: "Item will be reminded later",
      });
    },
    onError: (error) => {
      toast({
        title: "Failed to remind item later",
        description: error.message,
        variant: "destructive",
      });
    },
  });
  const { mutate: deleteItem } = useMutation({
    mutationFn: async (id: number) => await apiClient.deleteItem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["review-items"],
      });
      toast({
        title: "Item deleted",
      });
    },
    onError: (error) => {
      toast({
        title: "Failed to delete item",
        description: error.message,
        variant: "destructive",
      });
    },
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
      <ul className="flex flex-wrap justify-between gap-2 ">
        {isLoading ? (
          <p>Loading...</p>
        ) : (
          sorted?.map((item) => {
            const reviewDates = item.reviewDates.map((date) => new Date(date));
            const nextReviewDate = new Date(
              item.reviewDates[0],
            ).toLocaleString();

            return (
              <Drawer key={item.id}>
                <DrawerTrigger className="flex-grow w-full text-left md:w-auto hover:cursor-pointer">
                  <li>
                    <Card>
                      <CardHeader>
                        <CardTitle>{item.mainText}</CardTitle>
                      </CardHeader>
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
                        <div className="my-4">
                          <p className="leading-7 [&:not(:first-child)]:mt-6">
                            {parsingSubtext(item.subText)}
                          </p>
                          <p className="leading-7 [&:not(:first-child)]:mt-6">
                            Next review at{" "}
                            <Badge style={randomColor()}>
                              {nextReviewDate}
                            </Badge>
                          </p>
                        </div>

                        <section className="flex flex-col items-center my-4">
                          <h4 className="text-xl font-semibold tracking-tight text-black scroll-m-20">
                            Review Dates
                          </h4>

                          <div className="text-center">
                            <Calendar selected={reviewDates} />
                          </div>
                        </section>
                      </DrawerDescription>
                    </DrawerHeader>

                    <DrawerFooter>
                      <Button onClick={() => markAsMemorized(item.id)}>
                        Memorized
                      </Button>
                      <Button
                        onClick={() => remindMeAgainLater(item.id)}
                        variant="secondary"
                      >
                        Remind me again later
                      </Button>
                      <Button
                        onClick={() => deleteItem(item.id)}
                        variant="destructive"
                      >
                        Delete
                      </Button>
                    </DrawerFooter>
                  </div>
                </DrawerContent>
              </Drawer>
            );
          })
        )}
      </ul>
    </section>
  );
}

const parsingSubtext = (subText: string): ReactNode => {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  const urls = subText.match(urlRegex);

  if (urls) {
    const parsed = subText.split(urlRegex);

    return parsed.map((text, index) => {
      if (urls.includes(text)) {
        return (
          <a
            key={index}
            href={text}
            target="_blank"
            rel="noreferrer noopener"
            className="underline"
          >
            {text}
          </a>
        );
      } else {
        return <span key={index}>{text}</span>;
      }
    });
  }
};
