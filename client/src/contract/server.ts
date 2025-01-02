export interface ReviewItemDetail {
  id: number;
  mainText: string;
  subText: string;
  isRecurring: boolean;
  upcomingReviewDates: string[];
  notMemorizedDates: string[];
  memorizedDates: string[];
  skippedDates: string[];
}

export interface ReviewItem {
  id: number;
  mainText: string;
  subText: string;
  createDatetime: string;
  successCount: number;
  failCount: number;
  reviewDates: string[];
  isRecurring: boolean;
}

export interface TodayItem {
  id: number;
  mainText: string;
  status: Status;
}

export enum Status {
  MEMORIZED = "MEMORIZED",
  NOT_MEMORIZED = "NOT_MEMORIZED",
  NO_ACTION = "NO_ACTION",
}
