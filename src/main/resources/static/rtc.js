const ip = window.location.protocol + '//' + 
                   window.location.hostname + 
                   ':3000'

const socket = io(ip + "/mediasoup")

socket.on('connection-success', res => {
	console.log(res.socketId)
})