import { useQuery } from "@tanstack/react-query";
import { useState } from "react";

import { apiClient } from "@/api-client";

import AddItem from "@/components/add-item/add-item";
import { ItemList } from "@/components/item-list/item-list";

import { ModeToggle } from "@/components/mode-toogle";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

function App() {
  const [tab, setTab] = useState<"today" | "all">("today");
  const { data: items, isLoading: areItemsLoading } = useQuery({
    queryKey: ["review-items", tab],
    queryFn: () => apiClient.getItems(tab),
  });

  return (
    <main className="flex flex-col items-center w-full">
      <div className="w-full max-w-[710px]">
        <div className="flex items-start justify-between h-12 mb-2">
          <h1 className="text-4xl font-extrabold tracking-tight scroll-m-20 lg:text-5xl page-title">
            Review Notes
          </h1>

          <div className="flex self-center gap-x-2">
            <AddItem />
            <ModeToggle />
          </div>
        </div>

        <Separator className="my-4" />

        <Tabs
          defaultValue="today"
          className="w-full"
          value={tab}
          onValueChange={(value) => setTab(value as "today" | "all")}
        >
          <TabsList className="w-full">
            <TabsTrigger className="w-full" value="today">
              Today
            </TabsTrigger>

            <TabsTrigger className="w-full" value="all">
              All
            </TabsTrigger>
          </TabsList>

          <TabsContent value="today">
            <ItemList isLoading={areItemsLoading} items={items} />
          </TabsContent>

          <TabsContent value="all">
            <ItemList isLoading={areItemsLoading} items={items} />
          </TabsContent>
        </Tabs>
      </div>
    </main>
  );
}

export default App;
