import AddItem from "@/components/add-item/add-item";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ItemList } from "./components/item-list/item-list";

const queryClient = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <h1>Review Notes</h1>
      <AddItem />

      <ItemList />
    </QueryClientProvider>
  );
}

export default App;
