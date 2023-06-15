/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      fontFamily: {
        default: ["Public Sans", "Inter", "sans-serif"],
      },
    },
    colors: {
      transparent: "transparent",
      main: "#fff",
      black: "#000000",
      primary: "#86162c",
      secondary: "#28A4E2",
      darkBlue: "#032B3D",
      customDarkBlue: "#383874",
      customGreen: "#00B929",
      customRed: "#FF708B",
      customPurple: "#6A6A97",
      customBg: "#F6F7FB",
      customDisabled: "#BDC3CA",
      customBrown: "#696969",
    },
  },
  plugins: [],
};
