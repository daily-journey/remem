import type { Item } from "./model/server";

interface ApiClient {
  getItems(): Promise<Item[]>;
}

class FetchApiClient implements ApiClient {
  async getItems(): Promise<Item[]> {
    const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/items`, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data = await response.json();

    return data;
  }
}

export const apiClient = new FetchApiClient();
