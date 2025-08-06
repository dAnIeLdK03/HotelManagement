module.exports = {
  // ... други настройки
  module: {
    rules: [
      {
        test: /\.js$/,
        enforce: "pre",
        use: ["source-map-loader"],
        exclude: /node_modules/  // Добавете това, за да изключите node_modules
      },
      // ... други правила
    ]
  }
};