export interface Item {
  id: number;
  mainText: string;
  subText: string;
  createDatetime: string;
  successCount: number;
  failCount: number;
  reviewDates: string[];
  repeated: boolean;
}
