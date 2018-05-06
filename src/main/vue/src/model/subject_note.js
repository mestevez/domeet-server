import { Model } from 'vue-mc'

/**
 * SubjectNote model
 */
export default class SubjectNote extends Model {
  options () {
    return {
      identifier: 'note_id'
    }
  }

  routes () {
    return {
      save: '/app/meet/note/{note_id}',
      delete: '/app/meet/note/{note_id}'
    }
  }
}

export let SubjectNoteType = {
  COMMENT: 0,
  DECISION: 1,
  AGREEMENT: 2,
  UNSETTLED: 3
}
