<template>
  <div id="app">
    <router-view class="router-view" v-if="isRouterAlive"/>
  </div>
</template>

<script>
export default {
  name: 'App',
  provide() { // 注册一个方法
    return {
      reload: this.reload
    }
  },
  data() {
    return {
      isRouterAlive: true
    }
  },
  methods: {
    reload() {
      this.isRouterAlive = false
      this.$nextTick(function() {
        this.isRouterAlive = true
      })
    }
  },
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: -10px;
  margin-left: -10px;
}
</style>
