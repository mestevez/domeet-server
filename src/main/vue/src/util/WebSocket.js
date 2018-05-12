import _Vue from 'vue'
import VueNativeSock from 'vue-native-websocket'

let WebSocket = function () {}
export default WebSocket

WebSocket.install = function (Vue) {
  Vue.prototype.$initWebsocket = function (url, onpen) {
    _Vue.use(VueNativeSock, 'ws://' + self.location.host + url, {
      format: 'json',
      reconnection: true, // (Boolean) whether to reconnect automatically (false)
      reconnectionAttempts: 5, // (Number) number of reconnection attempts before giving up (Infinity),
      reconnectionDelay: 3000 // (Number) how long to initially wait before attempting a new (1000)
    })

    this.$socket.onopen = () => {
      setInterval(() => {
        this.$socket.sendObj({ message: 'ping' })
      }, 5000)
      if (typeof onpen === 'function') {
        onpen()
      }
    }
  }
}
