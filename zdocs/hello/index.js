const http = require("http")

const server = http.createServer((req, res) => {
    res.end("hello world nodejs\n")
})


server.listen(8090, () => {
    console.log("server running at port 8090")
})

