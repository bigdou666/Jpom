<template>
  <a-layout class="docker-layout">
    <a-layout-sider width="100px" style="min-height: calc(100vh - 85px)">
      <a-menu theme="light" mode="inline" class="docker-menu" v-model="menuKeyArray" @click="menuClick">
        <a-menu-item key="containers">
          <span class="nav-text">容器</span>
        </a-menu-item>
        <a-menu-item key="images">
          <span class="nav-text">镜像</span>
        </a-menu-item>
        <a-menu-item key="volumes">
          <span class="nav-text">卷</span>
        </a-menu-item>
        <a-menu-item key="networks">
          <span class="nav-text">网络</span>
        </a-menu-item>
        <a-menu-item key="info">
          <span class="nav-text">信息</span>
        </a-menu-item>
        <a-menu-item key="prune">
          <span class="nav-text">裁剪</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <!-- style="{ background: '#fff', padding: '10px' }"  -->
    <a-layout class="layout-content drawer-layout-content">
      <!-- <a-layout-header :style="{ background: '#fff', padding: 0 }" /> -->
      <!-- :style="{ margin: '24px 16px 0' }" -->
      <a-layout-content>
        <container v-if="menuKey === 'containers'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
        <images v-if="menuKey === 'images'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
        <volumes v-if="menuKey === 'volumes'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
        <info v-if="menuKey === 'info'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
        <networks v-if="menuKey === 'networks'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
        <prune v-if="menuKey === 'prune'" :id="this.id" :machineDockerId="this.machineDockerId" :visible="this.visible" :urlPrefix="this.urlPrefix" />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>
<script>
import Container from "./container";
import Images from "./images";
import Volumes from "./volumes";
import Info from "./info";
import Networks from "./networks";
import Prune from "./prune";
export default {
  props: {
    id: {
      type: String,
    },
    visible: {
      type: Boolean,
      default: false,
    },
    machineDockerId: {
      type: String,
    },
    urlPrefix: {
      type: String,
    },
  },
  components: {
    Container,
    Images,
    Volumes,
    Info,
    Networks,
    Prune,
  },
  data() {
    return {
      menuKeyArray: ["containers"],
      menuKey: "containers",
    };
  },
  mounted() {},
  methods: {
    menuClick(item) {
      this.menuKey = item.key;
    },
  },
};
</script>
<style scoped>
.docker-menu {
  height: 100%;
}
</style>
