import axios, { type AxiosInstance } from "axios";
import type { Item } from "./contract";

interface ApiClient {
  getItems(): Promise<Item[]>;
}

class AxiosApiClient implements ApiClient {
  private apiClient: AxiosInstance;
  constructor() {
    this.apiClient = axios.create({
      baseURL: import.meta.env.VITE_SERVER_URL,
      timeout: 1000,
      timeoutErrorMessage: "Request timed out",
    });
  }
  async getItems(): Promise<Item[]> {
    const response = await this.apiClient.get<Item[]>("/items");

    return response.data;
  }
}

export const axiosApiClient = new AxiosApiClient();
