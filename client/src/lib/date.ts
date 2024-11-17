export function getDatetime() {
  const date = new Date();
  const tzo = -date.getTimezoneOffset(),
    dif = tzo >= 0 ? "+" : "-",
    pad = function (num: number) {
      const norm = Math.abs(Math.floor(num));
      return (norm < 10 ? "0" : "") + norm;
    };

  const offset = dif + pad(tzo / 60) + ":" + pad(tzo % 60);

  return {
    isoString: date.toISOString(),
    offset: offset,
  };
}
