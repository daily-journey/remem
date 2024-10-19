import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ItemList } from "./components/item-list/item-list";

const queryClient = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ItemList />
    </QueryClientProvider>
  );
}

export default App;
