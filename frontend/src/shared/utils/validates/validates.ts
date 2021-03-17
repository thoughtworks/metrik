export const required = (errorMsg: string) => (value?: string) => (value ? null : errorMsg);
