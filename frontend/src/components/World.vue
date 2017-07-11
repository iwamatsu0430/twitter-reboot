<template>
  <div>hogehoge => {{ msg }}</div>
</template>

<script>
import axios from 'axios';

axios.get('http://localhost:9000/api/health')
  .then((res) => {
    // eslint-disable-next-line
    console.log(res)
  });

export default {
  name: 'world',
  data() {
    return {
      msg: 'fooo barr',
    };
  },
};
</script>
