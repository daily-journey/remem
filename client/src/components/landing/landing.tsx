import { UserPhase } from "@/App";
import SignInForm from "../auth/sign-in-form";
import SignUpForm from "../auth/sign-up-form";

interface Props {
  phase: UserPhase;
  changePhase: (phase: UserPhase) => void;
}

export default function Landing({ phase, changePhase }: Props) {
  return (
    <section className="min-h-screen">
      {/* Hero Section */}
      <section className="text-center bg-gradient-to-br from-pink-500 via-yellow-500 to-green-300 py-20">
        <h1 className="text-6xl font-extrabold tracking-tight lg:text-7xl">
          Re:Mem
        </h1>
        <p className="text-2xl mt-4">
          Repeat Memorization with an optimized review cycle
        </p>
      </section>

      <section className="py-10 px-4 mt-10">
        <img src="/remem_calendar.png" alt="calendar image" />
      </section>

      {/* Features Section */}
      <section className="py-10 px-4 mt-10">
        <h2 className="text-4xl font-bold text-center mb-8">Features</h2>
        <ol className="max-w-3xl mx-auto space-y-4 text-2xl pl-5">
          <li>Add an item to memorize</li>
          <li>Mark an item as memorized or not memorized</li>
          <li>Review cycle is set dynamically</li>
          <li>Check the review cycle through the calendar</li>
        </ol>
      </section>

      {phase === UserPhase.LogIn && (
        <SignInForm showSignUpForm={() => changePhase(UserPhase.SignUp)} />
      )}

      {phase === UserPhase.SignUp && (
        <SignUpForm onSignUpSuccess={() => changePhase(UserPhase.LogIn)} />
      )}
    </section>
  );
}
