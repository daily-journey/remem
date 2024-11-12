import AddItem from "@/components/add-item/add-item";
import { ModeToggle } from "@/components/mode-toogle";
import { ThemeProvider } from "@/components/theme-provider";
import { Separator } from "@/components/ui/separator";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ItemList } from "./components/item-list/item-list";

const queryClient = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider defaultTheme="system" storageKey="vite-ui-theme">
        <div className="flex items-start justify-between h-12 mb-2">
          <h1 className="text-4xl font-extrabold tracking-tight scroll-m-20 lg:text-5xl page-title">
            Review Notes
          </h1>

          <div className="self-center">
            <ModeToggle />
          </div>
        </div>

        <Separator className="my-4" />

        <AddItem />

        <Separator className="my-4" />

        <ItemList />
      </ThemeProvider>
    </QueryClientProvider>
  );
}

export default App;
