/** Java: Runnable. */
type Runnable = () => void;

export function run(): void {
  const open: Runnable = () => console.log('Opening file...');
  const save: Runnable = () => console.log('Saving file...');
  const close: Runnable = () => console.log('Closing file...');
  execute(open);
  execute(save);
  execute(close);
}

export function execute(command: Runnable): void {
  command();
}
