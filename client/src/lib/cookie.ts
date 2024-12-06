export function getCookieValue(key: string) {
  return (
    document.cookie
      .split("; ")
      .find((row) => row.startsWith(`${key}=`))
      ?.split("=")[1] ?? null
  );
}
