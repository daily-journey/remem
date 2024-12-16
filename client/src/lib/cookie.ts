export function getCookieValue(key: string) {
  const value = document.cookie
    .split("; ")
    .find((row) => row.startsWith(`${key}=`))
    ?.split("=")[1];

  if (!value) {
    return null;
  }

  return decodeURI(value);
}
