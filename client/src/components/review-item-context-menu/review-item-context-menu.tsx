import { useQueryClient } from "@tanstack/react-query";
import type { ReactNode } from "react";

import { Status, type TodayItem } from "@/contract/server";
import { useReviewItemMutation } from "@/hooks/use-review-item-mutation";

import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuSeparator,
  ContextMenuTrigger,
} from "@/components/ui/context-menu";
import { CircleArrowRight, CircleCheck, Trash2 } from "lucide-react";

interface Props {
  reviewItemId: number;
  reviewItemStatus: TodayItem["status"];
  children: ReactNode;
}
export default function ReviewItemContextMenu({
  reviewItemId,
  reviewItemStatus,
  children,
}: Props) {
  const queryClient = useQueryClient();
  const { deleteItem, notMemorized, markAsMemorized } = useReviewItemMutation();

  return (
    <ContextMenu>
      <ContextMenuTrigger>{children}</ContextMenuTrigger>
      <ContextMenuContent>
        {reviewItemStatus === Status.NO_ACTION && (
          <>
            <ContextMenuItem
              className="flex items-center gap-x-2"
              onClick={(e) => {
                e.stopPropagation();
                markAsMemorized(reviewItemId);
                queryClient.invalidateQueries({ queryKey: ["review-items"] });
              }}
            >
              <CircleCheck size={20} />
              Memorized
            </ContextMenuItem>
            <ContextMenuItem
              className="flex items-center gap-x-2"
              onClick={(e) => {
                e.stopPropagation();
                notMemorized(reviewItemId);
                queryClient.invalidateQueries({ queryKey: ["review-items"] });
              }}
            >
              <CircleArrowRight size={20} />
              Not Memorized
            </ContextMenuItem>
            <ContextMenuSeparator />
          </>
        )}

        <ContextMenuItem
          className="flex items-center gap-x-2"
          onClick={(e) => {
            e.stopPropagation();
            deleteItem(reviewItemId);
          }}
        >
          <Trash2 size={20} />
          Delete
        </ContextMenuItem>
      </ContextMenuContent>
    </ContextMenu>
  );
}
