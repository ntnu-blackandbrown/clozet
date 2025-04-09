declare module 'sockjs-client' {
  export default class SockJS {
    constructor(url: string, options?: any);
  }
}

declare module 'stompjs' {
  export interface Client {
    connect: (headers: any, connectCallback: (frame: any) => void, errorCallback: (error: any) => void) => void;
    disconnect: (callback?: () => void) => void;
    send: (destination: string, headers?: any, body?: string) => void;
    subscribe: (destination: string, callback: (message: any) => void, headers?: any) => any;
    debug: any;
  }

  export function over(ws: any): Client;
}
