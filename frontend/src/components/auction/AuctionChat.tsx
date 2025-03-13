"use client";

export default function AuctionChat({
  messages,
}: {
  messages: { id: number; sender: string; text: string; isMe: boolean }[];
}) {
  return (
    <div className="space-y-2">
      {messages.map((msg) => (
        <div
          key={msg.id}
          className={`flex ${
            msg.isMe ? "justify-end" : "justify-start"
          } items-start`}
        >
          <div
            className={`${
              msg.isMe
                ? "bg-blue-500 text-white"
                : "bg-gray-200 text-gray-700 border"
            } rounded-lg px-4 py-2 max-w-xs break-words`}
          >
            {!msg.isMe && (
              <p className="text-sm font-bold mb-1">{msg.sender}</p>
            )}
            <p className="text-sm">{msg.text}</p>
          </div>
        </div>
      ))}
    </div>
  );
}
