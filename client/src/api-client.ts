import { getDatetimeWithOffset } from "@/lib/date";
import type { Item } from "./model/server";

interface AddItemCommand {
  mainText: string;
  subText?: string;
}

interface ApiClient {
  getItems(criteria: "today" | "all"): Promise<Item[]>;
  addItem(item: AddItemCommand): Promise<void>;
}

class FetchApiClient implements ApiClient {
  async getItems(criteria: "today" | "all"): Promise<Item[]> {
    const date = criteria === "today" ? new Date().toISOString() : "";
    const query = date ? `?date=${date}` : "";

    const response = await fetch(
      `${import.meta.env.VITE_SERVER_URL}/items${query}`,
      {
        headers: {
          "Content-Type": "application/json",
        },
      },
    );
    const data = await response.json();

    return data;
  }

  async addItem(item: AddItemCommand): Promise<void> {
    const response = await fetch(`${import.meta.env.VITE_SERVER_URL}/items`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        ...item,
        createdDatetime: getDatetimeWithOffset(),
      }),
    });

    if (!response.ok) {
      throw new Error(`Failed to add item: ${response.statusText}`);
    }
  }
}

export const apiClient = new FetchApiClient();
