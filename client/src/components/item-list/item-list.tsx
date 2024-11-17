import type { ReactNode } from "react";

import ItemDetail from "@/components/item-detail/item-detail";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
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

import type { Item } from "@/model/server";

interface Props {
  items?: Item[];
  isLoading: boolean;
}

export const ItemList = ({ items, isLoading }: Props) => {
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
                            {parsingSubtext(item.subText)}
                          </p>
                          <p className="leading-7 [&:not(:first-child)]:mt-6">
                            Next review at{" "}
                            <Badge style={randomColor()}>
                              {nextReviewDate}
                            </Badge>
                          </p>
                        </div>

                        <ItemDetail data={item} />
                      </DrawerDescription>
                    </DrawerHeader>

                    <DrawerFooter>
                      <Button>Memorized</Button>
                      <Button variant="secondary">Remind me again later</Button>
                      <Button variant="destructive">Delete</Button>
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
};

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
