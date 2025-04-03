import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import MessageInput from '@/components/messaging/MessageInput.vue'

describe('MessageInput.vue', () => {
  it('renders input with correct placeholder', () => {
    const wrapper = mount(MessageInput)
    const input = wrapper.find('input')
    expect(input.exists()).toBe(true)
    expect(input.attributes('placeholder')).toBe('Type a message...')
  })

  it('emits "send-message" event when Enter key is pressed with non-empty message', async () => {
    const wrapper = mount(MessageInput)
    const input = wrapper.find('input')
    await input.setValue('Hello world')
    // Check that the button receives the active class when input is non-empty
    const button = wrapper.find('button.send-button')
    expect(button.classes()).toContain('active')

    // Simulate pressing Enter on the input
    await input.trigger('keyup.enter')

    // Assert that the event was emitted with the correct text
    expect(wrapper.emitted('send-message')).toBeTruthy()
    expect(wrapper.emitted('send-message')![0]).toEqual(['Hello world'])

    // Input should be cleared after sending
    expect(input.element.value).toBe('')
  })

  it('emits "send-message" event when send button is clicked with non-empty message', async () => {
    const wrapper = mount(MessageInput)
    const input = wrapper.find('input')
    await input.setValue('Test message')
    const button = wrapper.find('button.send-button')
    expect(button.classes()).toContain('active')

    // Simulate clicking the send button
    await button.trigger('click')

    // Assert that the event was emitted correctly
    expect(wrapper.emitted('send-message')).toBeTruthy()
    expect(wrapper.emitted('send-message')![0]).toEqual(['Test message'])

    // Input should be cleared after sending
    expect(input.element.value).toBe('')
  })

  it('does not emit "send-message" when input is empty or whitespace', async () => {
    const wrapper = mount(MessageInput)
    const input = wrapper.find('input')

    // Set input to only whitespace
    await input.setValue('    ')
    await input.trigger('keyup.enter')
    expect(wrapper.emitted('send-message')).toBeFalsy()

    // Also test clicking the send button in this case
    const button = wrapper.find('button.send-button')
    await button.trigger('click')
    expect(wrapper.emitted('send-message')).toBeFalsy()
  })
})
