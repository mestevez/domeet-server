<template>
  <v-card>
    <v-card-title primary-title>
      <v-container flex xs12>
        <div class="headline">{{ i18n.title_general }}</div>
        <v-layout row wrap>
          <v-flex xs12 sm7>
            <v-text-field
              name="meet_title"
              :label="i18n.label_title"
              type="text"
              v-model="app.meet_title"
              :rules="[() => app.meet_title.length > 0 || i18n.rule_required]"
              required
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm3>
            <v-select
              :items="app.meeting_types"
              :label="i18n.label_type"
              v-model="app.meet_type"
              required
            ></v-select>
            <input type="hidden" name="meet_type" :value="app.meet_type">
          </v-flex>
          <v-flex xs12 sm7>
            <v-menu
              ref="datepicker"
              lazy
              :close-on-content-click="false"
              v-model="datepicker"
              transition="scale-transition"
              offset-y
              full-width
              :nudge-right="40"
              min-width="290px"
              :return-value.sync="app.meeting_date"
            >
              <v-text-field
                slot="activator"
                :label="i18n.label_date"
                v-model="input_meeting_date"
                prepend-icon="event"
                readonly
                :rules="[() => app.meeting_date.length > 0 || i18n.rule_required]"
                required
              ></v-text-field>
              <input type="hidden" name="meeting_date" :value="app.meeting_date">
              <v-date-picker
                v-model="app.meeting_date"
                no-title
                scrollable
                :first-day-of-week="user.locale.firstdayofweek"
                :locale="user.locale.code"
              >
                <v-spacer></v-spacer>
                <v-btn flat color="primary" @click="datepicker = false">{{ i18n.btn_cancel }}</v-btn>
                <v-btn flat color="primary" @click="$refs.datepicker.save(app.meeting_date)">{{ i18n.btn_ok }}</v-btn>
              </v-date-picker>
            </v-menu>
          </v-flex>
          <v-flex xs12 sm3>
            <v-text-field
              name="meet_duration"
              :label="i18n.label_duration"
              v-model="app.meet_duration"
              type="number"
              suffix="min"
              step="5"
              min="0"
            ></v-text-field>
          </v-flex>
          <v-flex xs12 sm12>
            <v-text-field
              name="meet_description"
              :label="i18n.label_description"
              type="text"
              v-model="app.meet_description"
              multi-line
            ></v-text-field>
          </v-flex>
        </v-layout>
      </v-container>
    </v-card-title>
  </v-card>
</template>

<script>
import moment from 'moment'
const appData = window.appData || {}
export default {
  name: 'MeetGeneralEdit',
  data () {
    return {
      datepicker: false,
      input_meeting_date: '', // Date in ISO format for date picker
      app: Object.assign({
        meeting_date: '',
        meet_description: '',
        meet_duration: 30,
        meet_title: '',
        meet_type: null,
        meeting_types: []
      }, appData.app),
      user: Object.assign({
        locale: {
          dateformat: 'MM-DD-YYYY',
          code: 'en-en',
          firstdayofweek: 0
        }
      }, appData.user),
      i18n: Object.assign({
        title_general: 'General description',
        label_title: 'Title',
        label_type: 'Type',
        label_date: 'Date',
        label_duration: 'Duration',
        label_description: 'Description',
        btn_cancel: 'Cancel',
        btn_ok: 'Ok',
        rule_required: 'This field is required'
      }, appData.i18n)
    }
  },
  computed: {
    // Allow wathing app.meeting_date
    meeting_date () {
      return this.app.meeting_date
    }
  },
  watch: {
    meeting_date: function (isodate) {
      this.input_meeting_date = this.parseDate(isodate)
    }
  },
  methods: {
    parseDate: function (isodate) {
      return moment(isodate).format(this.user.locale.dateformat)
    }
  }
}
</script>

<style scoped>

</style>
