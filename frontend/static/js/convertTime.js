function convertTimeFormat(longIntTime) {
    var date = new Date(longIntTime)
    return date.getFullYear() + '-' + (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-'
    + (date.getDate() < 10 ? '0' +(date.getDate()) : date.getDate()) + ' ' + (date.getHours() < 10 ? '0' + (date.getHours()) : date.getHours()) + ':' + date.getMinutes() + ':' + date.getSeconds()
}
export {
    convertTimeFormat
}