import type { Item } from "./model/server";

interface AddItemCommand {
  mainText: string;
  subText?: string;
}

interface ApiClient {
  getItems(): Promise<Item[]>;
  addItem(item: AddItemCommand): Promise<void>;
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

  async addItem(item: AddItemCommand): Promise<void> {
    const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/items`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(item),
    });

    if (!response.ok) {
      throw new Error(`Failed to add item: ${response.statusText}`);
    }
  }
}

export const apiClient = new FetchApiClient();
