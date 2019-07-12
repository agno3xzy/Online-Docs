<template>
    <img class="background-img" src='https://s2.ax1x.com/2019/07/10/ZcG4qU.png' @load="showPage"> 
</template>

<script>
export default {
    data() {
        return {

        }
    },
    methods: {
        showPage() {
            (document.getElementById('loading')).style.display = "none" 
        }
    }
}
</script>

<style scoped>
    @import '../../static/css/keyframe.css';
.background-img {
    background-size: 100% 100%;
    height: 100%;
    position: fixed;
    width: 100%;
    left: 0;
    top: 0;
    min-width: 1400px;
    min-height: 800px;
    filter:contrast(70%);
}
</style>