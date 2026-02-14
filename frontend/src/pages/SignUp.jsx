import React, { useState, useEffect } from 'react';
import { Moon, Sun, Shield, Zap, Layout } from 'lucide-react';

function SignUp() {
  const [darkMode, setDarkMode] = useState(false);

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [darkMode]);

  return (
    <div className="min-h-screen transition-colors duration-500 bg-white dark:bg-[#020617] flex items-center justify-center p-6 font-sans">

      {/* Theme Toggle - Floating glass style */}
      <button
        onClick={() => setDarkMode(!darkMode)}
        className="absolute top-8 right-8 p-3 rounded-2xl bg-slate-100 dark:bg-slate-800/50 backdrop-blur-md border border-slate-200 dark:border-slate-700 text-slate-600 dark:text-indigo-300 hover:scale-105 transition-all shadow-sm"
      >
        {darkMode ? <Sun size={20} /> : <Moon size={20} />}
      </button>

      <div className="flex w-full max-w-6xl h-[700px] bg-white dark:bg-[#0B1120] rounded-[32px] overflow-hidden shadow-2xl border border-slate-100 dark:border-slate-800/60">

        {/* Left Side: Professional Branding & Trust */}
        <div className="hidden lg:flex lg:w-[45%] bg-gradient-to-b from-indigo-600 to-blue-700 dark:from-indigo-950 dark:to-[#020617] p-16 flex-col justify-between relative overflow-hidden">
          {/* Decorative radial glow to break up the "flat" dark look */}
          <div className="absolute top-0 left-0 w-full h-full bg-[radial-gradient(circle_at_top_left,rgba(99,102,241,0.15),transparent)]"></div>

          <div className="relative z-10">
            <div className="flex items-center gap-3 mb-16">
              <div className="w-10 h-10 bg-white/10 backdrop-blur-lg rounded-xl flex items-center justify-center border border-white/20">
                <Layout className="text-white" size={24} />
              </div>
              <span className="text-2xl font-bold text-white tracking-tight">FlowDesk</span>
            </div>

            <h2 className="text-4xl font-bold text-white leading-[1.15] mb-8">
              Orchestrate your <br />
              <span className="text-indigo-300">workflow at scale.</span>
            </h2>

            <div className="space-y-6">
              {[
                { icon: <Shield size={18} />, text: "SOC2 Type II Compliant Security" },
                { icon: <Zap size={18} />, text: "Real-time task synchronization" }
              ].map((item, i) => (
                <div key={i} className="flex items-center gap-4 text-indigo-100/80">
                  <div className="p-2 bg-white/5 rounded-lg border border-white/10">{item.icon}</div>
                  <span className="text-sm font-medium">{item.text}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="relative z-10 p-6 bg-white/5 backdrop-blur-sm rounded-2xl border border-white/10">
            <p className="text-indigo-200 text-sm italic">"FlowDesk transformed how our engineering team handles tickets. The automation is unmatched."</p>
            <p className="text-white text-xs mt-3 font-semibold">— CTO, TechCorp</p>
          </div>
        </div>

        {/* Right Side: High-Contrast Form */}
        <div className="flex-1 flex flex-col justify-center px-12 lg:px-24 bg-white dark:bg-[#0B1120]">
          <div className="max-w-md w-full mx-auto lg:mx-0">
            <div className="mb-10">
              <h1 className="text-3xl font-bold text-slate-900 dark:text-white tracking-tight">Create Account</h1>
              <p className="text-slate-500 dark:text-slate-400 mt-2 font-medium">Join the professional standard in desk management.</p>
            </div>

            <form className="space-y-5">
              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">Full Name</label>
                <input
                  type="text"
                  placeholder="Alex Rivera"
                  className="w-full px-4 py-3.5 rounded-xl bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all placeholder:text-slate-400"
                />
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">Work Email</label>
                <input
                  autoComplete="off"
                  name="email"
                  type="email"
                  placeholder="alex@flowdesk.com"
                  className="w-full px-4 py-3.5 rounded-xl bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all placeholder:text-slate-400"
                />
              </div>

              <div className="space-y-1.5 pb-2">
                <label className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400">Password</label>
                <input
                autoComplete="new-password"
                name="password"
                  type="password"
                  placeholder="Min. 8 characters"
                  className="w-full px-4 py-3.5 rounded-xl bg-slate-50 dark:bg-slate-900/50 border border-slate-200 dark:border-slate-800 text-slate-900 dark:text-white focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 outline-none transition-all placeholder:text-slate-400"
                />
              </div>

              <button className="w-full bg-indigo-600 hover:bg-indigo-500 text-white py-4 rounded-xl font-bold shadow-[0_10px_20px_rgba(79,70,229,0.3)] transition-all active:scale-[0.98] flex items-center justify-center gap-2">
                Continue to FlowDesk
              </button>
            </form>

            <p className="mt-8 text-center text-sm text-slate-500 dark:text-slate-400">
              Already have an account?
              <a href="/login" className="ml-1 text-indigo-600 dark:text-indigo-400 font-bold hover:text-indigo-500 underline underline-offset-4">Sign in</a>
            </p>
          </div>
        </div>

      </div>
    </div>
  );
}

export default SignUp;