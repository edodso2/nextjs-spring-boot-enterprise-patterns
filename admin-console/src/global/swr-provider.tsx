'use client';

import { SWRConfig } from 'swr';
import { ReactNode } from 'react';

const fetcher = (...args: [RequestInfo | URL, RequestInit?]) => 
  fetch(...args).then(res => res.json());

interface SWRProviderProps {
  children: ReactNode;
}

export const SWRProvider = ({ children }: SWRProviderProps) => {
  return (
    <SWRConfig value={{ fetcher }}>
      {children}
    </SWRConfig>
  );
};