export default {
    state:{
        docName: '',
        docOwner: '',
        docID: '',
        oldPath: '',
        newPath: ''
    },
    mutations: {
        setFilePath(state, filePath) {
            state.oldPath = filePath.oldPath
            state.newPath = filePath.newPath
        },
        removeFilePath(state) {
            state.oldPath = ''
            state.newPath = ''
        }
    }
}