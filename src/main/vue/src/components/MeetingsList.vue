<template>
  <v-card v-if="meetingsList.length &gt; 0" style="max-width: 600px" class="ma-2 pa-2">
    <v-card-title primary-title>
      <div>
        <h3 class="headline mb-0">{{ cardTitle }}</h3>
      </div>
    </v-card-title>
    <v-list two-line>
      <template v-for="(meetItem, index) in meetingsList">
        <v-divider v-if="index &gt; 0" inset :key="index"></v-divider>
        <v-list-tile avatar :key="meetItem.meet_id" :href="navURL.replace(/\{([^}]+)\}/g, meetItem.meet_id)">
          <v-list-tile-avatar>
            <img
              v-if="meetItem.meet_leader.user_photo != null"
              :src="meetItem.meet_leader.user_photo"
              alt=""
            >
            <v-icon v-else x-large>account_circle</v-icon>
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title v-html="meetItem.meet_title"></v-list-tile-title>
            <v-list-tile-sub-title v-html="meetItem.meet_description"></v-list-tile-sub-title>
          </v-list-tile-content>
        </v-list-tile>
      </template>
    </v-list>
  </v-card>
</template>

<script>
export default {
  name: 'MeetingsList',
  props: ['meetingsList', 'cardTitle', 'navURL']
}
</script>

<style scoped>

</style>
