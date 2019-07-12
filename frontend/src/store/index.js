import Vue from 'vue'
import vuex from 'vuex'
Vue.use(vuex);

import doc_store from './doc.js'

export default new vuex.Store({
    modules:{
        doc: doc_store
    }
})