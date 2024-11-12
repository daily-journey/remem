import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ItemList } from "./components/item-list/item-list";

const queryClient = new QueryClient();
function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <h1>Review Notes</h1>

      <ItemList />
    </QueryClientProvider>
  );
}

export default App;
