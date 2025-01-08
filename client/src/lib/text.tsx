import type { ReactNode } from "react";

export const parsingSubtext = (subText: string): ReactNode => {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  const urls = subText.match(urlRegex);

  if (urls) {
    const parsed = subText.split(urlRegex);

    return parsed.map((text, index) => {
      if (urls.includes(text)) {
        return (
          <a
            key={index}
            href={text}
            target="_blank"
            rel="noreferrer noopener"
            className="underline"
          >
            {text}
          </a>
        );
      }

      return <span key={index}>{text}</span>;
    });
  }

  return subText;
};
